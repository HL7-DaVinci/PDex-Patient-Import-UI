const CompressionPlugin = require("compression-webpack-plugin");

module.exports = {
	module: {
		rules: [
			{
				test: /\.less$/,
				loader: "less-loader"
			}
		],
		lintOnSave: "default",
		configureWebpack: {
			plugins: [
				new CompressionPlugin()
			]
		},
		css: {
			extract: false
		}
	}
};
