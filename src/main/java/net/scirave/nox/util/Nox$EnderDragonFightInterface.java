/*
 * -------------------------------------------------------------------
 * Nox
 * Copyright (c) 2024 SciRave
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * -------------------------------------------------------------------
 */

package net.scirave.nox.util;

import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.Vec3d;

public interface Nox$EnderDragonFightInterface {

    boolean isDragonKilled();

    boolean isConnectedCrystal(EndCrystalEntity crystal);

    boolean inDragonRange(Vec3d pos);

}
