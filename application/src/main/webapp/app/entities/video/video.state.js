(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('video', {
            parent: 'app',
            url: '/video',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Video'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/video/video.html',
                    controller: 'VideoController',
                    controllerAs: 'vm'
                }
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
