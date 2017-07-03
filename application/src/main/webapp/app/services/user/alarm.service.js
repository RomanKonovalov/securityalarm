(function () {
    'use strict';

    angular
        .module('securityalarmApp')
        .factory('Alarm', Alarm);

    Alarm.$inject = ['$resource'];

    function Alarm ($resource) {
        var service = $resource('api/alarms/:id', {}, {
            'query': {method: 'GET', isArray: true},
            'save': { method:'POST' },
            'update': { method:'PUT' },
            'delete':{ method:'DELETE'}
        });

        return service;
    }
})();
