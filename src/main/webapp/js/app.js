var app = angular.module('app', [ 'ui.bootstrap', 'pascalprecht.translate' ]);
app.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled : true,
        requireBase : false
    });
});
app.config([ '$translateProvider', function($translateProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix : 'l10n/',
        suffix : '.json'
    });
} ]);