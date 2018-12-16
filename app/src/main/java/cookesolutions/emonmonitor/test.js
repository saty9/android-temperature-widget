var roleHarvester = {

    /** @param {Creep} creep **/
    acquire: function(creep) {
        var sources = creep.room.find(FIND_SOURCES);
        var target = Math.floor(Math.random() * 2.0);
        creep.memory.target = sources[target];
    }

    /** @param {Creep} creep **/
    run: function(creep) {
        if (creep.carry.energy < creep.carryCapacity) {
            if (creep.harvest(creep.memory.target) == ERR_NOT_IN_RANGE) {
                creep.moveTo(creep.memory.target, {
                    visualizePathStyle: {
                        stroke: '#ffaa00'
                    }
                });
            }
        } else {
            var targets = creep.room.find(FIND_STRUCTURES, {
                filter: (structure) => {
                    return (structure.structureType == STRUCTURE_EXTENSION || structure.structureType == STRUCTURE_SPAWN || structure.structureType == STRUCTURE_TOWER) &&
                        structure.energy < structure.energyCapacity;
                }
            });
            if (targets.length > 0) {
                if (creep.transfer(targets[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(targets[0], {
                        visualizePathStyle: {
                            stroke: '#ffffff'
                        }
                    });
                }
            }
        }
    }

};

module.exports = roleHarvester;