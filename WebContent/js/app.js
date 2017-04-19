angular.module('app', [ 'ui.bootstrap' ]).config(function($locationProvider) {
	$locationProvider.html5Mode({
		enabled : true,
		requireBase : false
	});
});