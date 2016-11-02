package org.luwrain.im;

public interface Events {

	/**
	 * ������� ���������� ��� ������������� ������, 
	 * ����� ���� ��������� ��������� ���������� �������� ��� ����������
	 * @param message �������� ������
	 */
	void onError(String message);
	void onWarning(String message);
	/**
	 * ������� ���������� ����� ������ ������� ���� ������������� �����������
	 * @param message �������������� ���������
	 */
	void on2PassAuth(String message);
	/**
	 * ������� ���������� ��� ��������� �������� �����������
	 */
	void onAuthFinish();
}
