/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.mixin.lightsource;

import dev.lambdaurora.lambdynlights.DynamicLightSource;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements DynamicLightSource {
    private int lambdynlights$luminance;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void dynamicLightTick() {
        if (this.isOnFire() || this.isGlowing()) {
            this.lambdynlights$luminance = 15;
        } else {
            int luminance = 0;
            var eyePos = new BlockPos(this.getX(), this.getEyeY(), this.getZ());
            boolean submergedInFluid = !this.world.getFluidState(eyePos).isEmpty();
            for (var equipped : this.getItemsEquipped()) {
                if (!equipped.isEmpty())
                    luminance = Math.max(luminance, LambDynLights.getLuminanceFromItemStack(equipped, submergedInFluid));
            }

            this.lambdynlights$luminance = luminance;
        }

        int luminance = DynamicLightHandlers.getLuminanceFrom(this);
        if (luminance > this.lambdynlights$luminance)
            this.lambdynlights$luminance = luminance;

        if (!LambDynLights.get().config.hasEntitiesLightSource() && this.getType() != EntityType.PLAYER)
            this.lambdynlights$luminance = 0;
    }

    @Override
    public int getLuminance() {
        return this.lambdynlights$luminance;
    }
}