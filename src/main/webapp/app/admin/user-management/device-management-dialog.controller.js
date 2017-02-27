(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('DeviceManagementDialogController',DeviceManagementDialogController);

    DeviceManagementDialogController.$inject = ['$uibModalInstance', '$stateParams', 'Devices', 'Device', 'TrackingTypes', 'NotificationTypes'];

    function DeviceManagementDialogController ($uibModalInstance, $stateParams, Devices, Device, TrackingTypes, NotificationTypes) {
        var vm = this;

        vm.devices = Devices;

        vm.clear = clear;

        vm.trackingTypes = TrackingTypes;

        vm.notificationTypes = NotificationTypes;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSuccess (result) {
            vm.devices = Device.query({login : $stateParams.login});
        }

        vm.login = function (device) {
            Device.login({'login': device.name}, onSuccess);
        };

        vm.logout = function (device) {
            Device.logout({'login': device.name}, onSuccess);
        };

        vm.config = function (device) {
            Device.config({'login': device.name}, onSuccess);
        };

    }
})();
