package fw.loginserver.clientpackets;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javolution.text.TextBuilder;

public abstract class ClientBasePacket
{
	protected static ByteBuffer _buf;

	protected final void writeD(int value) {
		_buf.putInt(value);
	}

	protected final void writeH(int value) {
		_buf.putShort((short) value);
	}

	protected final void writeC(int value) {
		_buf.put((byte) value);
	}

	protected final void writeF(double value) {
		_buf.putDouble(value);
	}

	protected final void writeS(String text) {
		if (text == null) {
			_buf.putChar('\000');
		} else {
			final int len = text.length();
			for (int i = 0; i < len; i++)
				_buf.putChar(text.charAt(i));
			_buf.putChar('\000');
		}
	}

	protected final void writeB(byte[] data) {
		_buf.put(data);
	}

	protected final int readD() {
		try {
			return _buf.getInt();
		} catch (Exception e) {
		}
		return 0;
	}

	protected final int readC() {
		try {
			return _buf.get() & 0xFF;
		} catch (Exception e) {
		}
		return 0;
	}

	protected final int readH() {
		try {
			return _buf.getShort() & 0xFFFF;
		} catch (Exception e) {
		}
		return 0;
	}

	protected final double readF() {
		try {
			return _buf.getDouble();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	protected final String readS() {
		TextBuilder sb = new TextBuilder();
		char ch;
		try {
			while ((ch = _buf.getChar()) != 0)
				sb.append(ch);
		} catch (Exception e) {
		}
		return sb.toString();
	}

	protected final byte[] readB(int length) {
		byte[] result = new byte[length];
		try {
			_buf.get(result);
		} catch (Exception e) {
		}
		return result;
	}
	
	protected abstract void writeImpl();

	public final byte[] getMessage() {
		byte[] result;
		_buf = ByteBuffer.allocate(16 * 1024);
		_buf.order(ByteOrder.LITTLE_ENDIAN);
		try {
			// _buf.putShort((short)0);
			try {
				writeImpl();
				int padding = (_buf.limit() + 4) % 8;
				if(padding != 0)
					for(int i = padding; i < 8; i++)
						writeC(0x00);
				writeD(0);
			} catch (Exception e) {
				return null;
			}
			_buf.flip();
			// _buf.putShort((short)_buf.limit());
			result = new byte[(short) _buf.limit()];
			System.arraycopy(_buf.array(), 0, result, 0, result.length);

		} finally {
			_buf = null;
		}
		return result;
	}
}
