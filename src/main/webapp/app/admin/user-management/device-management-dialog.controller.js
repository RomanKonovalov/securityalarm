(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('DeviceManagementDialogController',DeviceManagementDialogController);

    DeviceManagementDialogController.$inject = ['$stateParams', '$uibModalInstance', 'entity', 'User', 'uuid4', 'Devices', 'Device'];

    function DeviceManagementDialogController ($stateParams, $uibModalInstance, entity, User, uuid4, Devices, Device) {
        var vm = this;

        vm.devices = Devices;

        vm.authorities = ['ROLE_USER', 'ROLE_ADMIN', 'ROLE_DEVICE'];
        vm.clear = clear;
        vm.languages = null;
        vm.save = save;
        vm.user = entity;

        vm.existedUser = $stateParams.user;

        var uuid = uuid4.generate();

        if (vm.existedUser) {
            vm.user.authorities = ['ROLE_DEVICE'];
        }

        vm.changeLogin = function () {
            vm.user.login = vm.user.description + '_' + uuid;
            vm.user.email = vm.user.login + '@localhost';
        };


        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSaveSuccess (result) {
            vm.isSaving = false;
            $uibModalInstance.close(result);
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function save () {
            vm.isSaving = true;
            if (vm.user.id !== null) {
                User.update(vm.user, onSaveSuccess, onSaveError);
            } else if (!vm.existedUser) {
                vm.user.langKey = 'en';
                User.save(vm.user, onSaveSuccess, onSaveError);
            } else {
                vm.user.user = vm.existedUser;
                Device.save(vm.user, onSaveSuccess, onSaveError);
            }
        }
    }
})();
