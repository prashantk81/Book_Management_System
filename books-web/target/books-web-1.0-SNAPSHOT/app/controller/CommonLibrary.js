"use strict";
App.controller("CommonLibrary", function ($scope, $timeout, Restangular, $stateParams, $rootScope, $state, $window) {
	/**
	 * View scope variables.
	 */
	$scope.asc = true;
	$scope.offset = 0;
	$scope.limit = 20;
	$scope.search = {
		text: "",
		read: false,
	};
	$scope.loading = false;
	$scope.books = [];
	$scope.total = -1;

	// A timeout promise is used to slow down search requests to the server
	// We keep track of it for cancellation purpose
	var timeoutPromise;

	$scope.loadBooks = function () {
		$scope.offset = 0;
		$scope.total = -1;
		$scope.books = [];
		$scope.pageBooks();
	};
	$scope.pageBooks = function (next) {
		if ($scope.loading || $scope.total == $scope.books.length) {
			// Avoid spamming the server
			return;
		}

		if (next) {
			$scope.offset += $scope.limit;
		}

		$scope.loading = true;
		Restangular.one("commonlibrary/list")
			.get({})
			.then(function (data) {
				$scope.books = $scope.books.concat(data.books);
				$scope.total = data.total;
				$scope.loading = false;
			});
	};

	$scope.giveRating = function (rating, id) {
		Restangular.one("commonlibrary/changeRating")
			.put({ id: id, rating: rating })
			.then(
				function () {
					$state.transitionTo("commonlibrary");
					$window.location.reload();
				},
				function (response) {
					alert(response.data.message);
				}
			);
	};
	/**
	 * Watch for search scope change.
	 */
	$scope.$watch(
		"search",
		function () {
			if (timeoutPromise) {
				// Cancel previous timeout
				$timeout.cancel(timeoutPromise);
			}

			// Call API later
			timeoutPromise = $timeout(function () {
				$scope.loadBooks();
			}, 200);
		},
		true
	);

	$scope.filterButton = function () {
		$state.go("add-book");
	};

	$scope.findAllBooks = function () {
		Restangular.one("commonlibrary/list")
			.get({})
			.then(function (data) {
				$window.location.reload();
				$scope.books = [];
				$scope.total = -1;
				$scope.books = $scope.books.concat(data.books);
				$scope.total = data.total;
				$scope.loading = false;
			});
	};
	
	$scope.vm1 = {
 options1 :[],
 selectedOptions1 :[],
 dropdownVisible1 : false
  }
  $scope.toggleDropdownAuthor = function() {
 Restangular.one('commonlibrary/allAuthor').get().then(function(data) {
  $scope.vm1.options1 = data.allAuthor.split('$$');
        $scope.vm1.options1 = $scope.vm1.options1
                     .filter(function (item, pos) { 
                    return $scope.vm1.options1.indexOf(item) == pos;});
        $scope.dropdownVisible1 = !$scope.dropdownVisible1;
        },
        function (response) {
            alert(response.data.message);
          }
        );
  };

  $scope.addOption1 = function(option) {
 if($scope.vm1.selectedOptions1.includes(option)){
  let i=0
  while (i < $scope.vm1.selectedOptions1.length) {
      if ($scope.vm1.selectedOptions1[i] === option) {
        $scope.vm1.selectedOptions1.splice(i, 1);
      } else {
        ++i;
      }
  }
 }
 else{
     $scope.vm1.selectedOptions1.push(option);
     }
  };
  
    $scope.vm2 = {
 options2 :[],
 selectedOptions2 :[],
 dropdownVisible2 : false
  }
  $scope.toggleDropdownGenre = function() {
 Restangular.one('commonlibrary/allGenre').get().then(function(data) {
  $scope.vm2.options2 = data.allGenre.split('$$');
        $scope.vm2.options2 = $scope.vm2.options2
                     .filter(function (item, pos) { 
                    return $scope.vm2.options2.indexOf(item) == pos;});
        $scope.dropdownVisible2 = !$scope.dropdownVisible2;
       
        },
        function (response) {
            alert(response.data.message);
          }
        );
  };

  $scope.addOption2 = function(option) {
 if($scope.vm2.selectedOptions2.includes(option)){
  let i=0
  while (i < $scope.vm2.selectedOptions2.length) {
      if ($scope.vm2.selectedOptions2[i] === option) {
        $scope.vm2.selectedOptions2.splice(i, 1);
      } else {
        ++i;
      }
  }
 }
 else{
     $scope.vm2.selectedOptions2.push(option);
     }
  };

  $scope.vm3 = {
 options3 :['>0', '>1', '>2', '>3', '>4', '>5', '>6', '>7', '>8', '>9'],
 selectedOptions3 :"",
 dropdownVisible3 : false
  }
  $scope.toggleDropdownRating = function() {
    $scope.dropdownVisible3 = !$scope.dropdownVisible3;
  };

  $scope.addOption3 = function(option) {
 if($scope.vm3.selectedOptions3 == option){
  $scope.vm3.selectedOptions3="";
 }
 else{
  $scope.vm3.selectedOptions3=option;
 }
  };
  
  $scope.findBookByAuthor = function(){
 let authorNames ="";
 let i=0
 while (i < $scope.vm1.selectedOptions1.length) {
     authorNames += $scope.vm1.selectedOptions1[i]
     authorNames += "$$"
     i++;
 } 
 Restangular.one('commonlibrary/author').post("",{authorNames : authorNames}).then(function(data) {
        $scope.books = [];
        $scope.total = -1;
    $scope.books = $scope.books.concat(data.books);
          $scope.total = data.total;
          $scope.loading = false;
        },
        function (response) {
            alert(response.data.message);
          }
        );
  }
  $scope.findBookByGenre = function(){
 let genreNames ="";
 let i=0
 while (i < $scope.vm2.selectedOptions2.length) {
     genreNames += $scope.vm2.selectedOptions2[i]
     genreNames += "$$"
     i++;
 } 
 Restangular.one('commonlibrary/genre').post("",{genreNames : genreNames}).then(function(data) {
 
        $scope.books = [];
        $scope.total = -1;
   $scope.books = $scope.books.concat(data.books);
          $scope.total = data.total;
          $scope.loading = false;
        },
        function (response) {
            alert(response.data.message);
          }
        );
  }
  $scope.findBookByRating = function(){
 if(!$scope.vm3.selectedOptions3==""){
  Restangular.one('commonlibrary/rating').post("",{rating : $scope.vm3.selectedOptions3}).then(function(data) {

         $scope.books = [];
         $scope.total = -1;
     $scope.books = $scope.books.concat(data.books);
           $scope.total = data.total;
           $scope.loading = false;
         },
         function (response) {
             alert(response.data.message);
           }
         );
        }
  }
	

	$scope.bookRanking = function (selectedOption){
	console.log(selectedOption)
	 Restangular.one("commonlibrary/rankingData")
	  .get({ param: selectedOption })
	  .then(function (data) {
	     $scope.books = [];
          $scope.total = -1;
          $scope.books = $scope.books.concat(data.books);
          $scope.total = data.total;
          $scope.loading = false;
	   },function (response) {
			console.error(response);
            alert(response.data.message);
          }
        );
	   };




	$scope.showPopup = function (bookId) {
		$state.transitionTo("commonlibrarybookview", { id: bookId });
	};
});
