package org.btwr.bwt_hct.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.btwr.shared_library.api.data.UpdateRequiringData;

public class RecentlyOnChoppingBlockCountdownData extends UpdateRequiringData<LivingEntity> {
    public static Codec<RecentlyOnChoppingBlockCountdownData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("cooldown").forGetter(RecentlyOnChoppingBlockCountdownData::getCountdown)
            ).apply(instance, RecentlyOnChoppingBlockCountdownData::new)
    );

    public static PacketCodec<ByteBuf, RecentlyOnChoppingBlockCountdownData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    private int countdown;

    public static final int maxCountDown = 40;

    public RecentlyOnChoppingBlockCountdownData(int cooldown) {
        this.countdown = cooldown;
    }

    public static RecentlyOnChoppingBlockCountdownData initialize() {
       return new RecentlyOnChoppingBlockCountdownData(0);
    }

    public int getCountdown() {
        return countdown;
    }

    @Override
    public void tick(LivingEntity entity) {
        if (countdown > 0) {
            countdown--;
        }
    }
}
