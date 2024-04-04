"use strict";
App.controller(
  "CommonLibraryBookView",
  function ($scope, Restangular, $stateParams, $state) {
    Restangular.one("commonlibrary", $stateParams.id)
      .get()
      .then(function (data) {
        $scope.data = data;
      });

    $scope.goBack = function () {
      $state.transitionTo("commonlibrary");
    };
  }
);
