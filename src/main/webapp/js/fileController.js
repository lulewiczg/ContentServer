angular.module('app').controller("fileController", function($http, $location, $scope, $modal, $window) {
    $scope.files = [];
    $scope.user;
    $scope.initPerformed = false;
    $scope.actualFolder;
    $scope.folders;
    $scope.roots = [];
    $scope.init = function() {
        $http.get('rest/login').then(function(result) {
            $scope.user = result.data;
            if ($scope.user == "") {
                $scope.user = undefined;
            }
        });
    };

    $scope.reload = function() {
        if (!$scope.initPerformed) {
            $scope.initPerformed = true;
            $http.get('rest/roots').then(function(result) {
                if (result.data !== "") {
                    $scope.roots = result.data;
                    if ($location.search().path === undefined) {
                        $location.search("path", $scope.roots[0]);
                    }
                }
            });
        }

        var path = $location.search().path;
        $scope.prepareItems(path);
    }

    $scope.$on('$locationChangeSuccess', function() {
        $scope.reload();
    });

    $scope.prepareItems = function(path) {
        if (path.endsWith("/")) {
            path = path.slice(0, -1);
        }
        var folders = path.split("/");
        $scope.actualFolder = folders[folders.length - 1];
        var limit;
        var url = "rest/roots?path=" + path;
        $http.get(url).then(function(result) {
            limit = result.data;
            console.log(limit);
            var actual = "";
            $scope.folders = [];
            for (var i = 0; i < folders.length; i++) {
                actual += folders[i] + "/";
                $scope.folders.push({
                    "name" : folders[i],
                    "path" : actual,
                    "disabled" : folders.length - i - 1 > limit
                });
            }
            $scope.folders[$scope.folders.length - 1].disabled = true;
        });

        console.log($scope.folders);
        $scope.load();
    }

    $scope.load = function() {
        $http.get('rest/files?path=' + $location.search().path).then(function(result) {
            if (result.data !== "") {
                $scope.files = result.data;
            }
        }, function() {
            alert("Nieprawidlowa siezka");
        });
    }

    $scope.getFileURL = function(file) {
        if (file.file == "true") {
            return "rest/files?path=" + file.path;
        } else {
            return "?path=" + file.path;
        }
    }

    $scope.logout = function() {
        $scope.user = undefined;
        $http.get('rest/logout');
        $window.location.href = $window.location.href.split("?")[0];
    }

    $scope.login = function() {
        var modalInstance = $modal.open({
            templateUrl : 'login.html',
            controller : 'loginController',
            size : 'm',
            resolve : {
                items : function() {
                    return $scope.user;
                }
            }
        }).result.then(function(result) {
            console.log(result);
            $scope.user = result;
            $scope.initPerformed = false;
            $scope.reload();
        });
    };

});