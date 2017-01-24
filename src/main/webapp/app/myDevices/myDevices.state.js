(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('myDevices', {
            parent: 'app',
            url: '/myDevices',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/myDevices/myDevices.html',
                    controller: 'MyDevicesController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                Devices: ['Device', function (Device) {
                    return Device.query();
                }],
                TrackingTypes: ['Util', function (Util) {
                    return Util.trackingTypes();
                }],
                NotificationTypes: ['Util', function (Util) {
                    return Util.notificationTypes();
                }]

            }
        });
    }
})();
