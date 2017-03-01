(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('MyAlarmsController', MyAlarmsController);

    MyAlarmsController.$inject = ['$scope', 'Alarm','Device', 'Devices', 'TrackingTypes', 'NotificationTypes'];

    function MyAlarmsController ($scope, Alarm, Device, Devices, TrackingTypes, NotificationTypes) {

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

    }
})();
