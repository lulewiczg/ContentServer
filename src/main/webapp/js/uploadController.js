angular.module('app').controller('uploadController', function($http, $scope, items, $modal, $modalInstance, $translate) {
    $scope.location = items;

    $scope.close = function() {
        $modalInstance.close();
    }
});