(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('DeviceManagementDialogController',DeviceManagementDialogController);

    DeviceManagementDialogController.$inject = ['$uibModalInstance', '$stateParams', 'Devices', 'Device'];

    function DeviceManagementDialogController ($uibModalInstance, $stateParams, Devices, Device) {
        var vm = this;

        vm.devices = Devices;

        vm.clear = clear;

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

    }
})();
