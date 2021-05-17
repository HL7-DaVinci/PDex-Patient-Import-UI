module.exports = {
	module: {
		rules: [
			{
				test: /\.less$/,
				loader: "less-loader"
			}
		],
		css: {
			extract: false
		}
	}
};
