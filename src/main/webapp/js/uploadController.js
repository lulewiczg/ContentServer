angular.module('app').controller('uploadController',
        function($http, $scope, items, $modal, $modalInstance, $translate) {
            $scope.location = items;
            $scope.valid = false;

            $scope.init = function() {
                $('#upload').change(function(input) {
                    setTimeout(function() {
                        $scope.$apply(function() {
                            $scope.valid = input.target.value !== '';
                        });
                    }, 100);
                });
                $('#uploadModalBtn').click(function() {
                    var form = $('#uploadForm');
                    $.ajax({
                        url : form.attr('action'),
                        type : 'POST',
                        data : new FormData(form[0]),
                        contentType : false,
                        processData : false,
                        success : $scope.uploadSuccess,
                        error : $scope.uploadFail
                    });
                });
            };

            $scope.uploadSuccess = function(e) {
                alert($translate.instant("upload.result.success"));
                window.location.reload();
            }

            $scope.uploadFail = function(e) {
                console.log(e);
                alert($translate.instant("upload.result.error"));
                return false;
            }

            $scope.close = function() {
                $modalInstance.close();
            };
        });