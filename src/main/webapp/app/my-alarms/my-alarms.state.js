(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('myAlarms', {
            parent: 'app',
            url: '/my-alarms',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/my-alarms/my-alarms.html',
                    controller: 'MyAlarmsController',
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
