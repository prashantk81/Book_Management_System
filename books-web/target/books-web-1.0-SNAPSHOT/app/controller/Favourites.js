'use strict';

App.controller('Favourites', function($scope, Restangular) {
	$scope.fetch = function(){
		var queryParams = {provider: $scope.provider};
		if ($scope.searchQueryType == 'audiobook'){
			Restangular.one('thirdparty/favourites/audiobooks').get(queryParams).then(function(data){
				$scope.searchData = data;
			}, function(error){
		  		console.log(error);
			})
		}else{
			Restangular.one('thirdparty/favourites/podcasts').get(queryParams).then(function(data){
				$scope.searchData = data;
			}, function(error){
		  		console.log(error);
			})
		}
	}
});