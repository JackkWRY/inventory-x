// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2025-07-15",
  devtools: { enabled: true },

  modules: ["@pinia/nuxt", "@vueuse/nuxt", "@nuxtjs/i18n"],

  // @ts-ignore - i18n module configuration
  i18n: {
    langDir: 'locales',
    locales: [
      { code: 'en', name: 'English', file: 'en.json' },
      { code: 'th', name: 'ไทย', file: 'th.json' }
    ],
    defaultLocale: 'en',
    strategy: 'no_prefix',
    detectBrowserLanguage: {
      useCookie: true,
      cookieKey: 'i18n_redirected',
      redirectOn: 'root'
    }
  },



  runtimeConfig: {
    public: {
      apiBaseUrl: process.env.API_BASE_URL || "http://localhost:8081/api/v1",
    },
  },

  css: ["~/assets/css/main.css"],

  app: {
    head: {
      title: "InventoryX - Stock Management System",
      meta: [
        { charset: "utf-8" },
        { name: "viewport", content: "width=device-width, initial-scale=1" },
        {
          name: "description",
          content: "Modern Stock Management System with DDD Architecture",
        },
      ],
    },
  },

  typescript: {
    strict: true,
    typeCheck: true,
  },
});
