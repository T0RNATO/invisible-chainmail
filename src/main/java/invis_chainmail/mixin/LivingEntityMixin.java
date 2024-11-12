package invis_chainmail.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Shadow private void setSyncedArmorStack(EquipmentSlot slot, ItemStack armor) {}
	@Shadow public boolean hasStatusEffect(RegistryEntry<StatusEffect> effect) {return false;}
	@Shadow private void sendEquipmentChanges() {}

	@Inject(at = @At("HEAD"), method = "method_30120", cancellable = true)
	private void init(CallbackInfo info, @Local(argsOnly = true, ordinal = 0) EquipmentSlot slot,
					  @Local(argsOnly = true, ordinal = 0) ItemStack stack,
					  @SuppressWarnings("LocalMayBeArgsOnly") @Local() List<Pair<EquipmentSlot, ItemStack>> list
	) {
		Item item = stack.getItem();
		if (this.hasStatusEffect(StatusEffects.INVISIBILITY) && (
				item.equals(Items.CHAINMAIL_BOOTS) ||
				item.equals(Items.CHAINMAIL_LEGGINGS)||
				item.equals(Items.CHAINMAIL_CHESTPLATE) ||
				item.equals(Items.CHAINMAIL_HELMET)))
		{
			if (slot.isArmorSlot()) {
				var itemA = new ItemStack(Items.AIR);
				list.add(Pair.of(slot, itemA));
				this.setSyncedArmorStack(slot, itemA);
				info.cancel();
			}
		}
	}

	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"), method = "getEquipmentChanges")
	private ItemStack changeSyncedArmour(ItemStack original) {
		var item = original.getItem();
		if (this.hasStatusEffect(StatusEffects.INVISIBILITY) && (
				item.equals(Items.CHAINMAIL_BOOTS) ||
				item.equals(Items.CHAINMAIL_LEGGINGS)||
				item.equals(Items.CHAINMAIL_CHESTPLATE) ||
				item.equals(Items.CHAINMAIL_HELMET)))
		{
			return new ItemStack(Items.AIR);
		}
		return original;
	}

	@Inject(at = @At("TAIL"), method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z")
	private void updateArmour(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
		if (effect.equals(StatusEffects.INVISIBILITY)) {
			this.sendEquipmentChanges();
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateAttributes()V"), method = "onStatusEffectRemoved")
	private void makeArmourVisible(StatusEffectInstance effect, CallbackInfo ci) {
		if (effect.equals(StatusEffects.INVISIBILITY)) {
			this.sendEquipmentChanges();
		}
	}
}