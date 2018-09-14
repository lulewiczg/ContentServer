angular.module('app').controller('loginController', function($http, $scope, items, $modal, $modalInstance, $translate) {
    $scope.appName = items;
    $scope.login = function() {
        $.post($scope.appName + 'rest/login', $scope.data, function(result) {
            $scope.user = result;
            $scope.close();
        }).error(function() {
            alert($translate.instant("error.login.invalid"))
        });
        $scope.data.password = undefined;
    }

    $scope.close = function() {
        $modalInstance.close($scope.user);
    }
});