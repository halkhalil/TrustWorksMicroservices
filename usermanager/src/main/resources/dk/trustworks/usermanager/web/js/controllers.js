/**
 * Created by hans on 22/12/15.
 */
angular.module('myApp')
    .controller('TodoController', ['$scope', '$http', function ($scope, $http) {

        $scope.newTodo = {};

        $scope.loadTodos = function () {
            $http.get('/userservice/api/users')
                .success(function (data, status, headers, config) {
                    $scope.todos = data;
                })
                .error(function (data, status, headers, config) {
                    alert('Error loading Todos');
                });
        };

        $scope.addTodo = function () {
            $http.post('todos', $scope.newTodo)
                .success(function (data, status, headers, config) {
                    $scope.newTodo = {};
                    $scope.loadTodos();
                })
                .error(function (data, status, headers, config) {
                    alert('Error saving Todo');
                });
        };

        $scope.deleteTodo = function (todo) {
            $http.delete('todos/' + todo.id)
                .success(function (data, status, headers, config) {
                    $scope.loadTodos();
                })
                .error(function (data, status, headers, config) {
                    alert('Error deleting Todo');
                });
        };

        $scope.loadTodos();
    }]);