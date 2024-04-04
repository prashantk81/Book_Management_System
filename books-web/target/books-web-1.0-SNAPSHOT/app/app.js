"use strict";

/**
 * Sismics Books application.
 */
var App = angular
  .module(
    "books",
    // Dependencies
    [
      "ui.router",
      "restangular",
      "ngSanitize",
      "colorpicker.module",
      "ui.keypress",
      "ui.validate",
    ]
  )

  /**
   * Configuring modules.
   */
  .config(function ($stateProvider, $httpProvider, RestangularProvider, $sceDelegateProvider) {
    // Configuring UI Router
    $sceDelegateProvider.resourceUrlWhitelist([
	  'https://podz-content.spotifycdn.com/**',
	  'https://anchor.fm/s/dc690258/podcast/**',
	  'https://audio-ssl.itunes.apple.com/itunes-assets/**']);
    $stateProvider
      .state("main", {
        url: "",
        views: {
          page: {
            templateUrl: "partial/main.html",
            controller: "Main",
          },
        },
      })
      .state("bookaddmanual", {
        url: "/book/addmanual",
        views: {
          page: {
            templateUrl: "partial/book.edit.html",
            controller: "BookAddManual",
          },
        },
      })
      .state("book", {
        url: "/book/:tag",
        views: {
          page: {
            templateUrl: "partial/book.html",
            controller: "Book",
          },
        },
      })
      .state("bookview", {
        url: "/book/view/:id",
        views: {
          page: {
            templateUrl: "partial/book.view.html",
            controller: "BookView",
          },
        },
      })
      .state("bookedit", {
        url: "/book/edit/:id",
        views: {
          page: {
            templateUrl: "partial/book.edit.html",
            controller: "BookEdit",
          },
        },
      })
      .state("bookadd", {
        url: "/book/add/:isbn",
        views: {
          page: {
            templateUrl: "partial/book.add.html",
            controller: "BookAdd",
          },
        },
      })
      .state("login", {
        url: "/login",
        views: {
          page: {
            templateUrl: "partial/login.html",
            controller: "Login",
          },
        },
      })
      .state("register", {
        url: "/register",
        views: {
          page: {
            templateUrl: "partial/register.html",
            controller: "Register",
          },
        },
      })
      .state("tag", {
        url: "/tag",
        views: {
          page: {
            templateUrl: "partial/tag.html",
            controller: "Tag",
          },
        },
      })
      .state("commonlibrary", {
        url: "/commonlibrary",
        views: {
          page: {
            templateUrl: "partial/commonlibrary.html",
            controller: "CommonLibrary",
          },
        },
      })
      .state("add-book", {
        url: "/book/view/:id",
        views: {
          page: {
            templateUrl: "partial/add-book.html",
            controller: "AddBookLibrary",
          },
        },
      })
      .state("commonlibrarybookview", {
        url: "/commonlibrary/:id",
        views: {
          page: {
            templateUrl: "partial/commonlibrary.book.view.html",
            controller: "CommonLibraryBookView",
          },
        },
      })
      .state("settings", {
        url: "/settings",
        abstract: true,
        views: {
          page: {
            templateUrl: "partial/settings.html",
            controller: "Settings",
          },
        },
      })
      .state("settings.default", {
        url: "",
        views: {
          settings: {
            templateUrl: "partial/settings.default.html",
            controller: "SettingsDefault",
          },
        },
      })
      .state("settings.account", {
        url: "/account",
        views: {
          settings: {
            templateUrl: "partial/settings.account.html",
            controller: "SettingsAccount",
          },
        },
      })
      .state("settings.session", {
        url: "/session",
        views: {
          settings: {
            templateUrl: "partial/settings.session.html",
            controller: "SettingsSession",
          },
        },
      })
      .state("settings.log", {
        url: "/log",
        views: {
          settings: {
            templateUrl: "partial/settings.log.html",
            controller: "SettingsLog",
          },
        },
      })
      .state("settings.user", {
        url: "/user",
        views: {
          settings: {
            templateUrl: "partial/settings.user.html",
            controller: "SettingsUser",
          },
        },
      })
      .state("settings.user.edit", {
        url: "/edit/:username",
        views: {
          user: {
            templateUrl: "partial/settings.user.edit.html",
            controller: "SettingsUserEdit",
          },
        },
      })
      .state("settings.user.add", {
        url: "/add",
        views: {
          user: {
            templateUrl: "partial/settings.user.edit.html",
            controller: "SettingsUserEdit",
          },
        },
      })
      .state('spotify', {
	    url: '/spotify',
	    views: {
	      'page': {
	        templateUrl: 'partial/spotify.html',
	        controller: 'Spotify'
	      }
	    }
	  })
	  .state('itunes', {
	    url: '/itunes',
	    views: {
	      'page': {
	        templateUrl: 'partial/itunes.html',
	        controller: 'ITunes'
	      }
	    }
	  })
	  .state('favourites', {
	    url: '/favourites',
	    views: {
	      'page': {
	        templateUrl: 'partial/favourites.html',
	        controller: 'Favourites'
	      }
	      }
	  });

    // Configuring Restangular
    RestangularProvider.setBaseUrl("api");

    // Configuring $http to act like jQuery.ajax
    $httpProvider.defaults.headers.post["Content-Type"] =
      "application/x-www-form-urlencoded;charset=utf-8";
    $httpProvider.defaults.headers.put["Content-Type"] =
      "application/x-www-form-urlencoded;charset=utf-8";
    $httpProvider.defaults.transformRequest = [
      function (data) {
        var param = function (obj) {
          var query = "";
          var name, value, fullSubName, subName, subValue, innerObj, i;

          for (name in obj) {
            value = obj[name];

            if (value instanceof Array) {
              for (i = 0; i < value.length; ++i) {
                subValue = value[i];
                fullSubName = name;
                innerObj = {};
                innerObj[fullSubName] = subValue;
                query += param(innerObj) + "&";
              }
            } else if (value instanceof Object) {
              for (subName in value) {
                subValue = value[subName];
                fullSubName = name + "[" + subName + "]";
                innerObj = {};
                innerObj[fullSubName] = subValue;
                query += param(innerObj) + "&";
              }
            } else if (value !== undefined && value !== null) {
              query +=
                encodeURIComponent(name) +
                "=" +
                encodeURIComponent(value) +
                "&";
            }
          }

          return query.length ? query.substr(0, query.length - 1) : query;
        };

        return angular.isObject(data) && String(data) !== "[object File]"
          ? param(data)
          : data;
      },
    ];
  })

  /**
   * Application initialization.
   */
  .run(function ($rootScope, $state, $stateParams) {
    $rootScope.$state = $state;
    $rootScope.$stateParams = $stateParams;
  });
