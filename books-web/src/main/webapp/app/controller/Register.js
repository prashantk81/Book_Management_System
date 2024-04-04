'use strict'

App.controller('Register', function($scope, $state, $stateParams, Restangular) {
	$scope.register = function(){
		var promise = Restangular.one('user/create-account').put($scope.user) 
		promise.then(function(){
			alert('user created')
			$state.transitionTo('login');
		}, function(error){
			alert(error.data.message);
			console.error(error.data.message);
		})
		}
});