(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('MyDevicesController', MyDevicesController);

    MyDevicesController.$inject = ['$scope', 'Device', 'Devices'];

    function MyDevicesController ($scope, Device, Devices) {

        $scope.devices = Devices;

        $scope.saveDevice = function (device) {
            Device.update(device, function () {
                $scope.devices = Device.query();
            });
        };

        $scope.configDevice = function (device) {
            Device.config({'login': device.name});
        };

        $scope.reboot = function (device) {
            Device.reboot({'login': device.name});
        };

        $scope.halt = function (device) {
            Device.halt({'login': device.name});
        };

    }
})();
