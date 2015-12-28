var myApp = angular.module('myApp', ['ngRoute']);

myApp.config(['$routeProvider', '$locationProvider',
    function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/userservice/home', {
                templateUrl: 'home.html',
                controller: 'TodoController'
            })
            .otherwise({
                redirectTo: 'home'
            });

    }]);