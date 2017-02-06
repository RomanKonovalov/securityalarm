(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('map', {
            parent: 'entity',
            url: '/map',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Map'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/map/map.html',
                    controller: 'MapController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                Account: ['Principal', function (Principal) {
                    return Principal.identity(false);
                }],
                devices: ['Device', function (Device) {
                    return Device.query();
                }]
            }
        });
    }

})();
