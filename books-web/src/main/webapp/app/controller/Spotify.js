'use strict';

App.controller('Spotify', function($scope, $sce, Restangular) {
	$scope.favourites= {}
	$scope.searchSpotify = function() {
		var queryParams = {query:$scope.searchQueryString, type: $scope.searchQueryType, provider:'spotify', };
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
				for (let i = 0; i < $scope.searchData.episodes.length; ++i){
					$scope.searchData.episodes[i].isFavourite = false;
				}
			}
		}, function(error){
	  		console.log(error)
		});
	
	}
	
	$scope.favouritePodcast = function(data) {
		data.provider = 'spotify';
		data.isFavourite = !data.isFavourite;
		
		Restangular.one('thirdparty/favourites/podcast').post("", data).then(function(data){
			console.log(data)
			
		}, function(error){
	  		console.log(error)
		});
	}
	
	$scope.favouriteAudiobook = function(data) {
		data.provider = 'spotify';
		data.isFavourite = !data.isFavourite;
		data.previewUrl = '';
		
		Restangular.one('thirdparty/favourites/audiobook').post("", data).then(function(data){
			console.log(data)
			
		}, function(error){
	  		console.log(error)
		});
	}
});