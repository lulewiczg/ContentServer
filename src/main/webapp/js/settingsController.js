angular.module('app').controller('settingsController', function($http, $scope, items, $modal, $modalInstance) {
	$scope.user = items;
	$scope.settings = [];
	$scope.init = function() {
		$.get('rest/settings', function(result) {
			$scope.settings = result;
			$('.modal-body').click();
		}).error(function() {
			alert("Błont")
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
		$.post('rest/settings', result, function(result) {
			alert("OK");
		}).error(function() {
			alert("Błont")
		});
	}
});