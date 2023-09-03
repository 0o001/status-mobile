module.exports = {
    presets: ['module:metro-react-native-babel-preset'],
    plugins: ['react-native-reanimated/plugin',
    '@babel/plugin-proposal-logical-assignment-operators'],
    env: {
        test: {
            presets: [
                '@babel/preset-react',
                [
                    '@babel/preset-env',
                    {
                        targets: {
                            node: '14',
                        },
                        // Ensure all ES2021 features are transpiled
                        // for older environments.
                        spec: true,
                    },
                ],
            ],
            // If there are any ES2021 features that require
            // plugins, you can add them here.
            plugins: [
                '@babel/plugin-proposal-logical-assignment-operators',
            ],
        },
    },
};
