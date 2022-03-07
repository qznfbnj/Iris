package net.coderbot.iris.compat.sodium.impl.vertex_format.entity_xhfp.writer;

import me.jellysquid.mods.sodium.client.model.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.client.model.vertex.buffer.VertexBufferWriterUnsafe;
import net.coderbot.iris.compat.sodium.impl.vertex_format.IrisModelVertexFormats;
import net.coderbot.iris.compat.sodium.impl.vertex_format.entity_xhfp.EntityVertexSink;
import net.coderbot.iris.compat.sodium.impl.vertex_format.entity_xhfp.QuadViewEntity;
import net.coderbot.iris.vertices.IrisVertexFormats;
import net.coderbot.iris.vertices.NormalHelper;
import org.lwjgl.system.MemoryUtil;

public class EntityVertexBufferWriterUnsafe extends VertexBufferWriterUnsafe implements EntityVertexSink {
	private final QuadViewEntity quad = new QuadViewEntity();
	private static final int STRIDE = IrisVertexFormats.ENTITY.getVertexSize();
	float midU = 0;
	float midV = 0;

	public EntityVertexBufferWriterUnsafe(VertexBufferView backingBuffer) {
		super(backingBuffer, IrisModelVertexFormats.ENTITIES);
	}

	@Override
	public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
		long i = this.writePointer;

		midU += u;
		midV += v;

		MemoryUtil.memPutFloat(i, x);
		MemoryUtil.memPutFloat(i + 4, y);
		MemoryUtil.memPutFloat(i + 8, z);
		MemoryUtil.memPutInt(i + 12, color);
		MemoryUtil.memPutFloat(i + 16, u);
		MemoryUtil.memPutFloat(i + 20, v);
		MemoryUtil.memPutInt(i + 24, overlay);
		MemoryUtil.memPutInt(i + 28, light);
		MemoryUtil.memPutInt(i + 32, normal);
		MemoryUtil.memPutShort(i + 36, (short) -1);
		MemoryUtil.memPutShort(i + 38, (short) -1);

		this.advance();
	}

	@Override
	public void endQuad(int length, float normalX, float normalY, float normalZ) {
		long i = this.writePointer;

		quad.setup(writePointer, STRIDE);

		int tangent = quad.computeTangent(normalX, normalY, normalZ);

		for (long vertex = 0; vertex < length; vertex++) {
			MemoryUtil.memPutInt(i - 4 - STRIDE * vertex, tangent);
		}

		for (long vertex = 0; vertex < length; vertex++) {
			MemoryUtil.memPutFloat(i - 12 - STRIDE * vertex, midU / 4F);
			MemoryUtil.memPutFloat(i - 8 - STRIDE * vertex, midV / 4F);
		}

		midU = 0;
		midV = 0;
	}
}
