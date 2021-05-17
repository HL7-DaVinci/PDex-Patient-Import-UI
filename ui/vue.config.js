const path = require("path");
const { createProxyMiddleware } = require("http-proxy-middleware");
const CompressionPlugin = require("compression-webpack-plugin");
const StylelintPlugin = require("stylelint-webpack-plugin");

module.exports = {
	devServer: {
		port: 8081,
		after(app) {
			const proxy = createProxyMiddleware({
				target: "https://localhost:8443",
				secure: false
			});
			app.use("/api", proxy);
			app.use("/fhir", proxy);
		}
	},
	lintOnSave: process.env.NODE_ENV === "development" ? "warning" : "default",
	css: {
		loaderOptions: {
			less: {
				lessOptions: {
					modifyVars: {
						hack: `true; @import '${path.resolve("./src/assets/less/vant-theme/index.less")}';`
					}
				}
			}
		}
	},
	configureWebpack: {
		plugins: [
			new CompressionPlugin(),
			new StylelintPlugin({
				files: ["./src/**/*.{vue,scss,less}"]
			})
		]
	}
};
