(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('MyDevicesController', MyDevicesController);

    MyDevicesController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'Alarm','Device', 'Devices', 'TrackingTypes', 'NotificationTypes'];

    function MyDevicesController ($scope, Principal, LoginService, $state, Alarm, Device, Devices, TrackingTypes, NotificationTypes) {

        $scope.devices = Devices;

        $scope.trackingTypes = TrackingTypes;

        $scope.notificationTypes = NotificationTypes;

        $scope.activateAlarm = function (device) {
            device.alarm.device = {'id': device.id, 'login': device.name};
            Alarm.save(device.alarm, function () {
                $scope.devices = Device.query();
            });
        };

        $scope.deactivateAlarm = function (device) {
            Alarm.delete({'id': device.alarm.id}, function () {
                $scope.devices = Device.query();
            });
        };

        $scope.updateAlarm = function (alarm) {
            if (!alarm.id) {
                return;
            }
            Alarm.update(alarm, function () {
                $scope.devices = Device.query();
            });
        };

        $scope.saveDevice = function (device) {
            Device.update(device, function () {
                $scope.devices = Device.query();
            });
        };

        $scope.configDevice = function (device) {
            Device.config({'login': device.name});
        };

    }
})();
