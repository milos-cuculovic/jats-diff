package main.diff_L1_L2.ui;

/*****************************************************************************************
 *
 *   This file is part of jats-diff project.
 *
 *   jats-diff is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   jats-diff is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU LESSER GENERAL PUBLIC LICENSE for more details.

 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with jats-diff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 *****************************************************************************************/

import java.io.Serializable;

public class Parameters implements Serializable {

	private static final long serialVersionUID = 1L;

	private String configPath = null;
	private boolean diff = false;
	private boolean merge = false;
	private boolean stdout = false;
	private boolean xslt = false;
	private boolean outputXslt = false;
	private String originalPath = null;
	private String modifiedPath = null;
	private String deltaPath = null;
	private String markupPath = null;
	private String xsltPath = null;
	private String xsltOutputPath = null;

	public String getConfigPath() {
		return configPath;
	}

	public String getDeltaPath() {
		return deltaPath;
	}

	public String getMarkupPath() {
		return markupPath;
	}

	public String getModifiedPath() {
		return modifiedPath;
	}

	public String getOriginalPath() {
		return originalPath;
	}

	public String getXsltOutputPath() {
		return xsltOutputPath;
	}

	public String getXsltPath() {
		return xsltPath;
	}

	public boolean isDiff() {
		return diff;
	}

	public boolean isMerge() {
		return merge;
	}

	public boolean isOutputXslt() {
		return outputXslt;
	}

	public boolean isStdout() {
		return stdout;
	}

	public boolean isXslt() {
		return xslt;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public void setDeltaPath(String deltaPath) {
		this.deltaPath = deltaPath;
	}

	public void setDiff(boolean diff) {
		this.diff = diff;
	}

	public void setMarkupPath(String markupPath) {
		this.markupPath = markupPath;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	public void setModifiedPath(String modifiedPath) {
		this.modifiedPath = modifiedPath;
	}

	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	public void setOutputXslt(boolean outputXslt) {
		this.outputXslt = outputXslt;
	}

	public void setStdout(boolean stdout) {
		this.stdout = stdout;
	}

	public void setXslt(boolean xslt) {
		this.xslt = xslt;
	}

	public void setXsltOutputPath(String xsltOutputPath) {
		this.xsltOutputPath = xsltOutputPath;
	}

	public void setXsltPath(String xsltPath) {
		this.xsltPath = xsltPath;
	}

}
