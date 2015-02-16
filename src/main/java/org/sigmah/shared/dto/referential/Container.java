package org.sigmah.shared.dto.referential;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.Objects;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Container implements IsSerializable {
	
	public static enum Type {
		PROJECT,
		ORG_UNIT
	}
	
	private int id;
	private String name;
	private String fullName;
	private Type type;

	protected Container() {
	}

	public Container(int id, String name, String fullName, Type type) {
		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.type = type;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + this.id;
		hash = 17 * hash + (this.type != null ? this.type.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Container other = (Container) obj;
		if (this.id != other.id) {
			return false;
		}
		return this.type == other.type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
}
