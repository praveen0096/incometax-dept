package io.mosip.credential.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.mosip.credential.util.Base64Adapter;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 
 * BIR class with Builder to create data
 * 
 * @author Ramadurai Pandian
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BIRType", propOrder = { "version", "cbeffversion", "birInfo", "bdbInfo",  "bdb",
		"sb" ,"birs","sbInfo","others"})
@XmlRootElement(name = "BIR")
@Data
@NoArgsConstructor
@JsonDeserialize(builder = BIR.BIRBuilder.class)
public class BIR implements Serializable {

	@XmlElement(name = "Version")
	private VersionType version;
	@XmlElement(name = "CBEFFVersion")
	private VersionType cbeffversion;
	@XmlElement(name = "BIRInfo", required = true)
	private BIRInfo birInfo;
	@XmlElement(name = "BDBInfo")
	private BDBInfo bdbInfo;
	@XmlElement(name = "BDB")
	@XmlJavaTypeAdapter(Base64Adapter.class)
	private byte[] bdb;
	@XmlElement(name = "SB")
	private byte[] sb;
	@XmlElement(name = "BIR")
	protected List<BIR> birs;
	@XmlElement(name = "SBInfo")
	private SBInfo sbInfo;
	@XmlElement(name = "Others")
	private Map<String, Object> others;

	public BIR(BIRBuilder birBuilder) {
		this.version = birBuilder.version;
		this.cbeffversion = birBuilder.cbeffversion;
		this.birInfo = birBuilder.birInfo;
		this.bdbInfo = birBuilder.bdbInfo;
		this.bdb = birBuilder.bdb;
		this.sb = birBuilder.sb;
		this.sbInfo = birBuilder.sbInfo;
		this.setOthers(birBuilder.others);
	}

	public static class BIRBuilder {
		private VersionType version;
		private VersionType cbeffversion;
		private BIRInfo birInfo;
		private BDBInfo bdbInfo;
		private byte[] bdb;
		private byte[] sb;
		private SBInfo sbInfo;
		private Map<String, Object> others;

		public BIRBuilder withOther(String key, Object value) {
			if(Objects.isNull(others))
				this.others = new HashMap<>();
			
			this.others.put(key, value);
			return this;
		}
		
		public BIRBuilder withOthers(Map<String, Object> others) {
			if(Objects.isNull(this.others))
				this.others = new HashMap<>();
			if(!Objects.isNull(others))
				this.others.putAll(others);
			return this;
		}

		public BIRBuilder withVersion(VersionType version) {
			this.version = version;
			return this;
		}

		public BIRBuilder withCbeffversion(VersionType cbeffversion) {
			this.cbeffversion = cbeffversion;
			return this;
		}

		public BIRBuilder withBirInfo(BIRInfo birInfo) {
			this.birInfo = birInfo;
			return this;
		}

		public BIRBuilder withBdbInfo(BDBInfo bdbInfo) {
			this.bdbInfo = bdbInfo;
			return this;
		}

		public BIRBuilder withBdb(byte[] bdb) {
			this.bdb = bdb;
			return this;
		}

		public BIRBuilder withSb(byte[] sb) {
			this.sb = sb;
			return this;
		}

		public BIRBuilder withSbInfo(SBInfo sbInfo) {
			this.sbInfo = sbInfo;
			return this;
		}

		public BIR build() {
			return new BIR(this);
		}

	}

}
