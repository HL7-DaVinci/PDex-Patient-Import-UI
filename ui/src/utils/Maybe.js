const unlift = x => maybeProto.isPrototypeOf(x) ? x.value : x;


const Maybe = (...xs) => {
	const m = Object.create(maybeProto);
	const values = xs.map(unlift);
	if (xs.length === 0) {
		m._value = [null];
	} else if (values.some(y => y === undefined || y === null)) {
		m._value = [null];
	} else {
		m._value = values;
	}
	return m;
};


Maybe.first = (...xs) => Maybe(xs.map(unlift).find(x => x !== null && x !== undefined));


const maybeProto = {
	get value() {
		return this._value.length === 1 ? this._value[0] : this._value;
	},
	then(k) {
		if (this.value === null) {
			return this;
		}
		return Maybe(k(...this._value));
	}
};


export default Maybe;
