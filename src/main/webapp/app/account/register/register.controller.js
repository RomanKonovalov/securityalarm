(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('RegisterController', RegisterController);


    RegisterController.$inject = ['$scope', '$timeout', 'Auth', 'LoginService'];

    function RegisterController ($scope, $timeout, Auth, LoginService) {



        var vm = this;

        vm.doNotMatch = null;
        vm.error = null;
        vm.errorUserExists = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.registerAccount = {};
        vm.success = null;

        $scope.locationpickerOptions = {
            location: {
                latitude: 53.90071589999999,
                longitude: 30.331359799999973
            },
            inputBinding: {
                latitudeInput: $('#us1-lat'),
                longitudeInput: $('#us1-lon'),
                locationNameInput: $('#us1-address')
            },
            radius: 0,
            enableAutocomplete: true,
            autocompleteOptions: {
                //types: ['(cities)'],
                componentRestrictions: {country: 'by'}
            }
        };

        $timeout(function (){angular.element('#login').focus();});

        function register () {
            if (vm.registerAccount.password !== vm.confirmPassword) {
                vm.doNotMatch = 'ERROR';
            } else {
                vm.registerAccount.langKey =  'en' ;
                vm.doNotMatch = null;
                vm.error = null;
                vm.errorUserExists = null;
                vm.errorEmailExists = null;

                Auth.createAccount(vm.registerAccount).then(function () {
                    vm.success = 'OK';
                }).catch(function (response) {
                    vm.success = null;
                    if (response.status === 400 && response.data === 'login already in use') {
                        vm.errorUserExists = 'ERROR';
                    } else if (response.status === 400 && response.data === 'e-mail address already in use') {
                        vm.errorEmailExists = 'ERROR';
                    } else {
                        vm.error = 'ERROR';
                    }
                });
            }
        }
    }
})();
