(ns test-helpers.unit
  (:require [cljs.test :as cljs-test]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.walk :as walk]))

(defn- subscription-name->test-name
  [sub-name]
  (->> [(namespace sub-name)
        (name sub-name)
        "test"]
       (remove nil?)
       (map #(string/replace % #"\." "-"))
       (string/join "-")))

(defmacro ^:private testing-subscription
  [description & body]
  `(cljs.test/testing ~description
     (test-helpers.unit/restore-app-db (fn [] ~@body))))

(s/fdef deftest-sub
  :args (s/cat :sub-name keyword?
               :args     (s/coll-of symbol? :count 1)
               :body     (s/* any?)))

(defmacro deftest-sub
  "Defines a test based on `sub-name`, executes `body` and restores the app db.

  Any usage of the `cljs.test/testing` macro inside `body` will be modified to
  also make sure the app db is restored and the subscription cache is reset.

  Expressions in `body` will have access to `sub-name`, which should be used to
  avoid needlessly repeating the subscription name.

  Example:

  ```clojure
  (require '[test-helpers.unit :as h])

  (h/deftest-sub :wallet/sorted-tokens
    [sub-name]
    (testing \"sorts tokens by name, lowercased\"
      ;; Arrange
      (swap! rf-db/app-db assoc-in [<db-path>] <value>)

      ;; Act and Assert
      (is (= <expected> (rf/sub [sub-name])))))
  ```"
  [sub-name args & body]
  `(let [sub-name# ~sub-name]
     (cljs.test/deftest ~(symbol (subscription-name->test-name sub-name))
       (let [~args [sub-name#]]
         (test-helpers.unit/restore-app-db
          (fn []
            ~@(clojure.walk/postwalk-replace
               {'cljs.test/testing `testing-subscription
                'testing           `testing-subscription}
               body)))))))

(defmacro use-log-fixture
  "Register log fixture which allows inspecting all calls to `taoensso.timbre/log`.

  Usage: Simply call this macro once per test namespace, and use the
  `test-helpers.unit/logs` atom to deref the collection of all logs for the
  test under execution.

  In Clojure(Script), we can rely on fixtures for each `cljs.deftest`, but not
  for individual `cljs.testing` macro calls. So keep that in mind when testing
  for log messages."
  []
  `(cljs.test/use-fixtures
    :each
    {:before test-helpers.unit/log-fixture-before
     :after  test-helpers.unit/log-fixture-after}))

(defmacro run-test-sync
  "Wrap around `re-frame.test/run-test-sync` to make it work with our aliased
  function `utils.re-frame/dispatch`.

  This macro is a must, because the original implementation uses `with-redefs`
  pointing to the original re-frame `dispatch` function, but our event handlers
  are dispatching using our aliased function.

  If tests run using the original `run-test-sync`, then all bets are off, and
  tests start to behave erratically."
  [& body]
  `(day8.re-frame.test/run-test-sync
    (with-redefs [utils.re-frame/dispatch re-frame.core/dispatch]
      ~@body)))

;; TODO: https://github.com/gphilipp/react-native-with-clojurescript
;; See the section about using spec to test all functions (render them).
#_(defmethod cljs-test/assert-expr 'match
    ;; Match `value` against `?schema`.
    ;;
    ;; Usage:
    ;;   (deftest validate-string-test
    ;;     (is (h/match [:string] 10)))
    ;;
    ;;   => FAIL
    ;;      expected: "should be a string" actual: 10
    [_env msg [_ ?schema value]]
    `(let [value#    ~value
           msg#      ~msg
           ?schema#  ~?schema
           valid?#   (malli.core/validate ?schema# value#)
           expected# (-> ?schema#
                         (malli.core/explain value#)
                         (malli.error/humanize))
           message#  (when-not valid?#
                       (clojure.string/trimr
                        (str (if msg#
                               (str msg# "\n"))
                             (with-out-str
                               (test-helpers.unit/explain ?schema# value#)))))]
       (cljs-test/do-report
        {:actual   value#
         :expected expected#
         :message  message#
         :type     (if valid?# :pass :fail)})))
