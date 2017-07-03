(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider', '$locationProvider'];

    function stateConfig($stateProvider, $locationProvider) {
        $locationProvider.html5Mode(true);
        $stateProvider.state('app', {
            abstract: true,
            views: {
                'navbar@': {
                    templateUrl: 'app/layouts/navbar/navbar.html',
                    controller: 'NavbarController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ]
            }
        });
    }
})();
