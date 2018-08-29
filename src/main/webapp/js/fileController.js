angular.module('app').controller("fileController", function($http, $location, $scope, $modal, $window) {
    $scope.files = [];
    $scope.user;
    $scope.initPerformed = false;
    $scope.actualFolder;
    $scope.folders;
    $scope.admin;
    $scope.context;
    $scope.appName = ''
    $scope.roots = [];
    $scope.init = function() {
        if ($scope.appName === '') {
            var loc = window.location.pathname.indexOf('/', 1);
            $scope.appName = window.location.pathname.substring(0, loc + 1);
        }
        $http.get($scope.appName + 'rest/login').then(function(result) {
            $scope.user = result.data;
            if ($scope.user == "") {
                $scope.user = undefined;
            }
            $scope.resolveAdmin();
        });
    };

    $scope.resolveAdmin = function() {
        if ($scope.user === "admin") {
            $scope.admin = true;
            $http.get($scope.appName + 'rest/context').then(function(result) {
                $scope.context = result.data;
                if (!$scope.context.endsWith('/')) {
                    $scope.context += '/';
                }
            }, function(result) {
                alert("Nie można pobrać kontekstu!");
                console.log(result);
            });
        } else {
            $scope.admin = false;
        }
    }
    $scope.reload = function() {
        if (!$scope.initPerformed) {
            $scope.initPerformed = true;
            $http.get($scope.appName + 'rest/roots').then(function(result) {
                if (result.data !== "") {
                    $scope.roots = result.data;
                    if ($location.search().path === undefined) {
                        $location.search("path", $scope.roots[0]);
                    }
                }
            }, function(result) {
                alert("Błąd!");
                console.log(result);
            });
        }

        var path = $location.search().path;
        if (path) {
            $scope.prepareItems(path);
        }
    }

    $scope.$on('$locationChangeSuccess', function() {
        $scope.reload();
    });

    $scope.prepareItems = function(path) {
        if (path.endsWith("/")) {
            path = path.slice(0, -1);
        }
        var folders = path.replace(/\\/g, '/').split('/');
        $scope.actualFolder = folders[folders.length - 1];
        var limit;
        var url = $scope.appName + "rest/roots?path=" + path;
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
        }, function(result) {
            alert("Błąd!");
            console.log(result);
        });

        console.log($scope.folders);
        $scope.load();
    }

    $scope.load = function() {
        $http.get($scope.appName + 'rest/files?path=' + $location.search().path).then(function(result) {
            if (result.data !== "") {
                $scope.files = result.data;
            }
        }, function(result) {
            alert("Nieprawidlowa siezka");
            console.log(result);
        });
    }

    $scope.getFileURL = function(file) {
        if (file.file) {
            return $scope.appName + "rest/files?path=" + file.path;
        } else {
            return $scope.appName + "?path=" + file.path;
        }
    }

    $scope.logout = function() {
        $scope.user = undefined;
        $scope.admin = undefined;
        $http.get($scope.appName + 'rest/logout');
        $window.location.href = $window.location.href.split("?")[0];
    }

    $scope.login = function() {
        $modal.open({
            templateUrl : 'login.html',
            controller : 'loginController',
            size : 'm',
            resolve : {
                items : function() {
                    return $scope.appName;
                }
            }
        }).result.then(function(result) {
            console.log(result);
            $scope.user = result;
            $scope.initPerformed = false;
            $scope.reload();
            $scope.resolveAdmin();
        });
    };

    $scope.settings = function() {
        $modal.open({
            templateUrl : 'settings.html',
            controller : 'settingsController',
            size : 'lg',
            resolve : {
                items : function() {
                    return $scope.appName;
                }
            }
        });
    };

    $scope.getLogsUrl = function() {
        return $scope.appName + "rest/files?path=" + $scope.context + "WEB-INF/logs/log.txt";
    }
});