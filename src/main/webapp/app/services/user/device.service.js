(function () {
    'use strict';

    angular
        .module('securityalarmApp')
        .factory('Device', Device);

    Device.$inject = ['$resource'];

    function Device ($resource) {
        var service = $resource('api/devices/:id', {}, {
            'query': {method: 'GET', isArray: true},
            'save': { method:'POST' },
            'update': { method:'PUT' },
            'delete':{ method:'DELETE'}
        });

        return service;
    }
})();
