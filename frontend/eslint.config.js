// @ts-check
const eslint = require("@eslint/js");
const tseslint = require("typescript-eslint");
const angular = require("angular-eslint");
const tailwind = require("eslint-plugin-tailwindcss");

module.exports = tseslint.config(
  {
    files: ["**/*.ts"],
    extends: [
      eslint.configs.recommended,
      ...tseslint.configs.recommended,
      ...tseslint.configs.stylistic,
      ...angular.configs.tsRecommended,
    ],
    processor: angular.processInlineTemplates,
    rules: {
      "@angular-eslint/directive-selector": [
        "error",
        {
          type: "attribute",
          prefix: "app",
          style: "camelCase",
        },
      ],
      "@angular-eslint/component-selector": [
        "error",
        {
          type: "element",
          prefix: "app",
          style: "kebab-case",
        },
      ],
    },
  },
  {
    files: ["**/*.html"],
    extends: [
      ...angular.configs.templateRecommended,
      ...angular.configs.templateAccessibility,
      ...tailwind.configs["flat/recommended"],
    ],
    rules: {
      // Tailwind-specific rules (customized for v4 compatibility)
      "tailwindcss/classnames-order": "warn",
      "tailwindcss/enforces-shorthand": "warn", 
      "tailwindcss/no-contradicting-classname": "error",
      "tailwindcss/no-unnecessary-arbitrary-value": "warn",
      // Disable rules that might conflict with v4
      "tailwindcss/no-custom-classname": "off", // Allow custom classes during v4 transition
    },
    settings: {
      tailwindcss: {
        config: "tailwind.config.js",
        cssFiles: [
          "**/*.css",
          "**/*.scss", 
          "!**/node_modules",
          "!**/dist"
        ],
        whitelist: [], // Add any custom classes you want to allow
      },
    },
  }
);
