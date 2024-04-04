'use strict';

App.controller('ITunes', function($scope, Restangular) {
	$scope.searchItunes = function() {
		var queryParams = {query:$scope.searchQueryString, type: $scope.searchQueryType, provider:'itunes', };
		console.log(queryParams);
		Restangular.one('thirdparty/search').get(queryParams).then(function(data) {
			console.log(data)
			$scope.searchData = data;
			
			if ($scope.searchQueryType == "audiobook"){
				for (let i = 0; i < $scope.searchData.audiobooks.length; ++i){
					$sce.trustAsHtml($scope.searchData.audiobooks[i].description);
					$scope.searchData.audiobooks[i].isFavourite = false;
				}
			}else{
				for (let i = 0; i < $scope.searchData.podcasts.length; ++i){
					$scope.searchData.podcasts[i].isFavourite = false;
				}
			}
			
		}, function(error){
	  		console.log(error)
		});
	}
	$scope.favouritePodcast = function(data) {
		data.provider = 'itunes';
		data.isFavourite = !data.isFavourite;
		
		Restangular.one('thirdparty/favourites/podcast').post("", data).then(function(data){
			console.log(data)
			
		}, function(error){
	  		console.log(error)
		});
	}
	
	$scope.favouriteAudiobook = function(data) {
		data.provider = 'itunes';
		data.isFavourite = !data.isFavourite;
		data.narrator = '';
		
		Restangular.one('thirdparty/favourites/audiobook').post("", data).then(function(data){
			console.log(data)
			
		}, function(error){
	  		console.log(error)
		});
	}
});