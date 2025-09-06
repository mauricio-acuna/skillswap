module.exports = {
  presets: ['module:metro-react-native-babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        root: ['./src'],
        alias: {
          '@components': './src/components',
          '@screens': './src/screens',
          '@services': './src/services',
          '@utils': './src/utils',
          '@types': './src/types',
          '@hooks': './src/hooks',
          '@store': './src/store',
          '@navigation': './src/navigation',
          '@styles': './src/styles',
          '@assets': './src/assets',
          '@i18n': './src/i18n',
          '@config': './src/config',
        },
      },
    ],
    'react-native-reanimated/plugin',
  ],
};
