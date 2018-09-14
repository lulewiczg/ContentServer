angular.module('app').controller('settingsController',
        function($http, $scope, items, $modal, $modalInstance, $translate) {
            $scope.appName = items;
            $scope.settings = [];
            $scope.init = function() {
                $.get($scope.appName + 'rest/settings', function(result) {
                    $scope.settings = result;
                    $('.modal-body').click();
                }).error(function() {
                    alert($translate.instant("error.settings.failedToLoad"));
                });
            }
            $scope.init();

            $scope.close = function() {
                $modalInstance.close();
            }

            $scope.save = function() {
                var fields = Object.keys($scope.settingsForm).filter(function(i) {
                    return !i.startsWith('$') && $scope.settingsForm[i].$dirty;
                });
                var result = [];
                fields.forEach(function(i) {
                    result.push({
                        'name' : i,
                        'value' : $scope.settingsForm[i].$modelValue
                    })
                });
                $.post($scope.appName + 'rest/settings', result, function(result) {
                    alert($translate.instant("info.settings.saved"));
                }).error(function() {
                    alert($translate.instant("error.settings.failedToSave"));
                });
            }
        });