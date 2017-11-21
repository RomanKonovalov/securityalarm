(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            },
            onEnter: ['DeviceTracker', 'Principal', function(DeviceTracker, Principal) {
                /*if (Principal.isAuthenticated()) {
                    DeviceTracker.subscribe();
                }*/
            }],
            onExit: ['DeviceTracker', 'Principal', function(DeviceTracker, Principal) {
                if (Principal.isAuthenticated()) {
                    DeviceTracker.unsubscribe();
                }
            }]
        });
    }
})();
