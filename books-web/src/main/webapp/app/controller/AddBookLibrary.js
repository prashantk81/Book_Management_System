"use strict";

App.controller(
  "AddBookLibrary",
  function ($scope, $http, $state, $stateParams, Restangular) {
    $scope.addBookInCommonLibrary = function (formData) {
      $scope.data = formData;
      console.log(formData);

      Restangular.one("commonlibrary", $stateParams.id)
        .post("", formData)
        .then(
          function () {
            $state.transitionTo("commonlibrary");
          },
          function (response) {
            alert(response.data.message);
          }
        );
    };
    $scope.closeButton = function () {
      $state.go("book");
    };
  }
);
