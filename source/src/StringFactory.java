public class StringFactory
{
	public String[] data;
	public boolean[] dataVisible;
	public float padding;
	
	public StringFactory(String[] data, int padding)
	{
		this.data = data;
		this.padding = padding;
		
		dataVisible = new boolean[data.length];
		for (int i = 0; i < data.length; i++) {
			dataVisible[i] = true;
		}
	}
	
	@Override
	public String toString()
	{
		String result = "";
		int visibleCount = 0;
		
		for (int i = 0; i < dataVisible.length; i++)
		{
			if (dataVisible[i]) {
				visibleCount++;
			}
		}
		
		for (int i = 0; i < data.length; i++)
		{
			if (dataVisible[i])
			{
				String paddingStr = "";
				for (int j = 0; j < ((padding - visibleCount) / 2); j++) {
					paddingStr += " ";
				}
				result += data[i] + paddingStr;
			}
		}
		
		return result;
	}
}