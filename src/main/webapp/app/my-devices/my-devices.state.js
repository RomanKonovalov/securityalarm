(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('myDevices', {
            parent: 'app',
            url: '/my-devices',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/my-devices/my-devices.html',
                    controller: 'MyDevicesController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                Devices: ['Device', function (Device) {
                    return Device.query();
                }]
            }
        });
    }
})();
