package org.valkyrienskies.core.api

import org.joml.Vector3dc
import org.valkyrienskies.core.game.ships.PhysShip

/**
 * Ship force inducer
 * The values shall be used every phyTick to apply forces to the ship.
 */
interface ShipForcesInducer {
    /**
     * Apply forces/torques on the physics tick
     * BE WARNED THIS GETS CALLED ON ANOTHER THREAD (the physics thread)
     *
     * @param forcesApplier Applies forces and torques to the ship
     * @param physShip The ship in the physics pipeline stage, use this for computing forces.
     *                 Please don't use [ShipData], [ShipObject] or anything else from the game stage pipeline.
     */
    fun applyForces(forcesApplier: ForcesApplier, physShip: PhysShip)
}

/**
 * Ship torque inducer
 * The values shall be used every phyTick to apply torque to the ship.
 */
interface ForcesApplier {

    fun applyRotDependentForce(force: Vector3dc)

    fun applyInvariantForce(force: Vector3dc)

    fun applyInvariantForceToPos(force: Vector3dc, pos: Vector3dc)

    fun applyRotDependentTorque(torque: Vector3dc)

    fun applyInvariantTorque(torque: Vector3dc)
}
