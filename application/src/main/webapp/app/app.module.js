(function() {
    'use strict';

    angular
        .module('securityalarmApp', [
            'ngStorage',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'infinite-scroll',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar',
            'uiGmapgoogle-maps',
            'angular-jquery-locationpicker',
            'checklist-model',
            'uuid',
            'betsol.intlTelInput',
            'ngTouch',
            'ngAnimate',
            'rgkevin.datetimeRangePicker',
            'currencyFormat',
            'ngFilesizeFilter'
        ])
        .config(config)
        .run(run)
        ;

    run.$inject = ['stateHandler'];

    config.$inject = ['uiGmapGoogleMapApiProvider', 'intlTelInputOptions'];

    function run(stateHandler) {
        stateHandler.initialize();
    }

    function config(uiGmapGoogleMapApiProvider, intlTelInputOptions) {
        uiGmapGoogleMapApiProvider.configure({
            key: 'AIzaSyCIWfhgaHMCsGASFQ6ZNhKA4MbjPcJLaz8',
            v: '3.20', //defaults to latest 3.X anyhow
            libraries: 'weather,geometry,visualization'
        });

        angular.extend(intlTelInputOptions, {
            geoIpLookup: function(callback) {
                $.get("http://ipinfo.io", function() {}, "jsonp").always(function(resp) {
                    var countryCode = (resp && resp.country) ? resp.country : "";
                    callback(countryCode);
                });
            },
            initialCountry: 'auto'
        });
    }

})();
